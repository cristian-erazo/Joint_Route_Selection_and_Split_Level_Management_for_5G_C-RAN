graph [
  node [
    id 0
    label "0"
    type 2
    prc 2
  ]
  node [
    id 1
    label "1"
    type 1
    prc 4
    ant 3
    prb 2
    x 23
    y 60
  ]
  node [
    id 2
    label "2"
    type 1
    prc 5
    ant 5
    prb 6
    x 44
    y 70
  ]
  node [
    id 3
    label "3"
    type 1
    prc 3
    ant 3
    prb 6
    x 57
    y 70
  ]
  node [
    id 4
    label "4"
    type 1
    prc 1
    ant 3
    prb 3
    x 78
    y 94
  ]
  node [
    id 5
    label "5"
    type 1
    prc 5
    ant 3
    prb 5
    x 21
    y 95
  ]
  edge [
    source 0
    target 1
    bandwith 602
    delay 211
  ]
  edge [
    source 0
    target 5
    bandwith 657
    delay 463
  ]
  edge [
    source 0
    target 3
    bandwith 485
    delay 129
  ]
  edge [
    source 0
    target 4
    bandwith 531
    delay 410
  ]
  edge [
    source 0
    target 2
    bandwith 543
    delay 166
  ]
]
