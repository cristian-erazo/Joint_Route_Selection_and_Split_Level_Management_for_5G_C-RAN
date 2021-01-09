graph [
  node [
    id 0
    label "0"
    type 2
    prc 4
  ]
  node [
    id 1
    label "1"
    type 1
    prc 5
    ant 2
    prb 6
    x 29
    y 21
  ]
  node [
    id 2
    label "2"
    type 1
    prc 1
    ant 2
    prb 5
    x 82
    y 112
  ]
  node [
    id 3
    label "3"
    type 1
    prc 2
    ant 8
    prb 6
    x 70
    y 87
  ]
  node [
    id 4
    label "4"
    type 1
    prc 1
    ant 2
    prb 4
    x 120
    y 113
  ]
  node [
    id 5
    label "5"
    type 1
    prc 1
    ant 4
    prb 2
    x 17
    y 27
  ]
  edge [
    source 0
    target 1
    bandwith 460
    delay 176
  ]
  edge [
    source 0
    target 5
    bandwith 507
    delay 159
  ]
  edge [
    source 0
    target 3
    bandwith 953
    delay 290
  ]
  edge [
    source 0
    target 4
    bandwith 353
    delay 244
  ]
  edge [
    source 0
    target 2
    bandwith 440
    delay 412
  ]
]
