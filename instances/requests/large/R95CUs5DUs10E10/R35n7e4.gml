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
    type 2
    prc 4
  ]
  node [
    id 2
    label "2"
    type 2
    prc 3
  ]
  node [
    id 3
    label "3"
    type 1
    prc 4
    ant 7
    prb 5
    x 99
    y 27
  ]
  node [
    id 4
    label "4"
    type 1
    prc 1
    ant 4
    prb 3
    x 66
    y 117
  ]
  node [
    id 5
    label "5"
    type 1
    prc 4
    ant 7
    prb 2
    x 28
    y 90
  ]
  node [
    id 6
    label "6"
    type 1
    prc 1
    ant 8
    prb 6
    x 18
    y 109
  ]
  edge [
    source 0
    target 4
    bandwith 643
    delay 177
  ]
  edge [
    source 1
    target 5
    bandwith 859
    delay 361
  ]
  edge [
    source 2
    target 3
    bandwith 436
    delay 275
  ]
  edge [
    source 2
    target 6
    bandwith 113
    delay 355
  ]
]
