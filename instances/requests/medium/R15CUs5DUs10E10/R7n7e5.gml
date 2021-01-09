graph [
  node [
    id 0
    label "0"
    type 2
    prc 5
  ]
  node [
    id 1
    label "1"
    type 2
    prc 2
  ]
  node [
    id 2
    label "2"
    type 1
    prc 1
    ant 3
    prb 2
    x 44
    y 76
  ]
  node [
    id 3
    label "3"
    type 1
    prc 1
    ant 4
    prb 4
    x 12
    y 92
  ]
  node [
    id 4
    label "4"
    type 1
    prc 5
    ant 2
    prb 4
    x 68
    y 100
  ]
  node [
    id 5
    label "5"
    type 1
    prc 4
    ant 3
    prb 4
    x 40
    y 25
  ]
  node [
    id 6
    label "6"
    type 1
    prc 4
    ant 9
    prb 4
    x 52
    y 40
  ]
  edge [
    source 0
    target 6
    bandwith 933
    delay 241
  ]
  edge [
    source 0
    target 2
    bandwith 531
    delay 393
  ]
  edge [
    source 1
    target 5
    bandwith 476
    delay 402
  ]
  edge [
    source 1
    target 3
    bandwith 599
    delay 306
  ]
  edge [
    source 1
    target 4
    bandwith 229
    delay 346
  ]
]
